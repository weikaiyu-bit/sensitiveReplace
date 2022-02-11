package com.wky.sensitive.aspect;

import com.wky.sensitive.annotation.SensitiveReplace;
import com.wky.sensitive.constant.ThreadLocalConstant;
import com.wky.sensitive.dto.Page;
import com.wky.sensitive.enums.DataTypeEnum;
import com.wky.sensitive.rule.Rule;
import com.wky.sensitive.rule.abstracts.BeforeSensitiveProcessor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;

/**
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-10 16:28
 */
@Component
@Aspect
public class SensitiveAspect {

    @Value("sensitive.target.point")
    public String point;

    public static ThreadLocal<Map<String, Object>> localVar = new ThreadLocal<>();

    @Autowired
    List<BeforeSensitiveProcessor> beforeSensitiveProcessors;

    private static Logger logger = LoggerFactory.getLogger(SensitiveAspect.class);


    // 切入点 拦截 controller
    @Pointcut("@annotation(com.wky.sensitive.annotation.SensitiveReplace)")
    public void SensitivePointCut() {

    }




    /**
     * 请求到达之前
     *
     * @throws Exception
     */
    @Before("SensitivePointCut()")
    public void before() {
        if (null != beforeSensitiveProcessors) {
            // 接口的回调
            beforeSensitiveProcessors.stream().forEach(x -> {
                Map<String, Object> map = localVar.get();
                if(null==map){
                    map=new HashMap<>();
                }
                map.put(ThreadLocalConstant.BOOL, x.bool());
                localVar.set(map);
            });
        }
//        ServiceLoader<BeforeSensitiveProcessor> load = ServiceLoader.load(BeforeSensitiveProcessor.class);
//        Iterator<BeforeSensitiveProcessor> iterator = load.iterator();
//        while (iterator.hasNext()) {
//            Map<String, Object> map = localVar.get();
//            map.put(ThreadLocalConstant.BOOL, iterator.next().bool());
//            localVar.set(map);
//        }
    }

    /**
     * after调用 用于拦截Controller层数据处理脱敏信息
     *
     * @param joinPoint  切点
     * @param jsonResult 返回值
     * @see [类、类#方法、类#成员]
     */
    @AfterReturning(pointcut = "SensitivePointCut()", returning = "jsonResult")
    public void afterReturn(JoinPoint joinPoint, Object jsonResult) throws Exception {
        //将 ThreadLocal 的值赋值给 jsonResult
        Map o = localVar.get();
        Object list = null;
        if (null != o) {
            list = o.get(ThreadLocalConstant.DATA);
        }
        if (list != null) {
            jsonResult = list;
        }
        if (null == o && jsonResult instanceof String) {
            String errMessage = "敏感信息处理异常:使用model返回数据时，需要将数据赋值给SensitiveAspect->localVar";
            logger.error(errMessage);
            throw new Exception(errMessage);
        }
        try {
            InnnerBean innnerBean = isSensitive(joinPoint);
            SensitiveReplace sensitiveReplace = innnerBean.sensitiveReplace;
            // 该用户没有配置脱敏角色,或者不存在注解，都不脱敏
            if (innnerBean.isFlag() == false || null == innnerBean.sensitiveReplace) {
                return;
            }
            // 脱敏处理
            filterSensitiveData(jsonResult, sensitiveReplace);
        } catch (Exception e) {
            logger.error("脱敏处理异常信息:{}", e.toString());
        }
    }

    /**
     * 脱敏处理
     *
     * @param jsonResult       要脱敏的数据
     * @param sensitiveReplace 注解信息
     */
    public void filterSensitiveData(Object jsonResult, SensitiveReplace sensitiveReplace) {

        // 替换规则权限定类名
        String[] rulePaths = sensitiveReplace.rulePath();

        // 替换的参数
        String[] params = sensitiveReplace.params();


        DataTypeEnum dataTypeEnum = sensitiveReplace.dataType();
        // page
        if (Objects.equals(dataTypeEnum, DataTypeEnum.PAGE)) {
            Page<Object> objectPage = (Page<Object>) jsonResult;
            filterSensitive(objectPage, Arrays.stream(rulePaths).collect(Collectors.toList()), params);
            return;
        }
        // 集合
        if (Objects.equals(dataTypeEnum, DataTypeEnum.LIST)) {
            List<Object> objectList = (List<Object>) jsonResult;
            filterSensitive(objectList, Arrays.stream(rulePaths).collect(Collectors.toList()), params);
            return;
        }

        // 对象
        if (Objects.equals(dataTypeEnum, DataTypeEnum.ENTITY)) {
            Object object = jsonResult;
            filterSensitive(object, Arrays.stream(rulePaths).collect(Collectors.toList()), params);
            return;
        }

    }

    /**
     * @param dataTarget 需要脱敏数据集
     * @param listType   脱敏类型
     * @param params     需要脱敏参数
     */
    public static void filterSensitive(Page<?> dataTarget, List<String> listType, String[] params) {
        List<?> dataSources = dataTarget.getResult();
        filterSensitive(dataSources, listType, params);
    }

    // 单个对象的方法重载
    public static void filterSensitive(Object dataTarget, List<String> listType, String[] params) {
        List<Object> dataSources = new ArrayList<>();
        dataSources.add(dataTarget);
        filterSensitive(dataSources, listType, params);
    }

    // 集合的重载方法
    public static void filterSensitive(List<?> dataSources, List<String> listType, String[] params) {
        for (int j = 0; j < dataSources.size(); j++) {
            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                String type = listType.get(i);
                // 对象的属性值
                String value = "";
                // 反射执行get方法：//get后首字母大写
                String getter = "get" + param.substring(0, 1).toUpperCase() + param.substring(1);
                Method curMethod = null;
                try {
                    curMethod = dataSources.get(j).getClass().getMethod(getter);
                    value = (String) curMethod.invoke(dataSources.get(j));
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                // 根据脱敏类路径规则实现脱敏
                value = setTypeSensitive(type, value);
                // 反射执行set方法
                String setter = "set" + param.substring(0, 1).toUpperCase() + param.substring(1);
                try {
                    Method curSetMethod = dataSources.get(j).getClass().getMethod(setter, String.class);
                    ;//get后首字母大写 //curVO实体对象、value：set的参数值
                    curSetMethod.invoke(dataSources.get(j), value);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String setTypeSensitive(String rulePath, String value) {
        if (org.springframework.util.StringUtils.isEmpty(value)) {
            return "";
        }
        Class<Rule> aClass = null;
        // 根据类路径反射实例化对象
        try {
            aClass = (Class<Rule>) Class.forName(rulePath);
        } catch (ClassNotFoundException e) {
            logger.error("获取替换规则实现类失败-不存在该实现类，异常：", e.getMessage());
        }
        try {
            Rule rule = aClass.newInstance();
            value = rule.processor(value);
        } catch (InstantiationException e) {
            logger.error("获取替换规则实现类失败，类路径实例化{}，异常：{}", rulePath, e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }


    /**
     * 判断 用户是否
     *
     * @param joinPoint 切点
     * @return true为脱敏，false为不脱敏
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static InnnerBean isSensitive(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Class<?>[] argTypes = new Class[joinPoint.getArgs().length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        Method method = null;
        try {
            method = joinPoint.getTarget().getClass()
                    .getMethod(joinPoint.getSignature().getName(), argTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        SensitiveReplace annotation = method.getAnnotation(SensitiveReplace.class);
        if (null != annotation) {
            Map<String, Object> map = localVar.get();
            boolean bool = true;
            if (null != map) {
                bool = (boolean) map.get(ThreadLocalConstant.BOOL);
            }
            return new InnnerBean(annotation, bool);
        }
        return new InnnerBean(null, false);
    }

    /**
     * 内部类封装注入信息
     *
     * @see [相关类/方法]
     * @since [产品/模块版本]
     */
    static class InnnerBean {
        private SensitiveReplace sensitiveReplace;// 模块代码

        private boolean flag = false; //默认不存在注解

        public InnnerBean(SensitiveReplace sensitiveReplace, boolean flag) {
            this.sensitiveReplace = sensitiveReplace;
            this.flag = flag;
        }

        public SensitiveReplace getSensitiveParam() {
            return sensitiveReplace;
        }

        public void setSensitiveParam(SensitiveReplace sensitiveParams) {
            this.sensitiveReplace = sensitiveReplace;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }
    }
}
