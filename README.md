# sensitiveReplace
sensitiveReplace是一个脱敏的工具插件，使用非常简单，开箱即用，无代码入侵，注解方式配置即可。
<br/>



## 使用方式
### 步骤一：引入maven依赖
  *注：必须选择1.0.0之后版本*
  ---
   ```java<!-- https://mvnrepository.com/artifact/io.github.weikaiyu-bit/sensitive-replace -->
<dependency>
    <groupId>io.github.weikaiyu-bit</groupId>
    <artifactId>sensitive-replace</artifactId>
    <version>1.0.0</version>
</dependency>
```
### 步骤二：注入SensitiveCoreBean bean

##### 1、若您使用的是springmvc,你将在你的配置文件中添加:
`<bean class="com.hgsoft.zengzhiyingyong.module.sensitive.aspect.SensitiveCoreBean" lazy-init="false"/>`

##### 2、若您使用的是springboot,请在项目中添加@Bean SensitiveCoreBean
```java
import com.wky.sensitive.aspect.SensitiveCoreBean;

@Configuration
public class SensitiveCoreBeanConfig
{
    @Bean
    public SensitiveCoreBean sensitiveCoreBean();
    {

        return new SensitiveCoreBean();
    }
}
```


*示例演示：*

1、你只需要在controller中添加`@SensitiveReplace`注解，并标识你所返回的数据类型，如下所示
 ```java
	@SensitiveReplace(dataType = DataTypeEnum.COMMON)
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public Page<CompanyInfo> query(CompanyInfo company, String pageNo, String pageSize) {
		Page<CompanyInfo> page = super.getPage(pageNo, pageSize);
		return companyInfoService.pageQuery(company, page);
	}
 ```
2、在你所返回的数据的实体中标识 `@Replace` 注解，如下所示
```java
  @Replace(rulePath = BankCardRuleImpl.class,description = "银行卡敏感替换")
	private String buyerBankAccount;
  
  @Replace(rulePath = NameRuleImpl.class,description = "名称敏感替换")
	private String buyerName;
  ...
 ```
  即可完成替换
  
  <br />
 
  <br />
  <br />
  
  ### 注解参数说明
  
  ##### 1、@SensitiveReplace

|  参数   | 类型  | 可选值  | 默认值 | 说明  |
|  ----   | ---- |  ----   | ---- | ----   |
| dataType  | DataTypeEnum |LIST,COMMON,ENTITY  |COMMON| 返回的数据 可选集合，统一返回，实体 三种类型 |
| key  | String | -- |result | dataType类型为COMMON时必填，默认值为 "result"，你需要告诉插件，你要替换的值字段key值叫什么，key值数据只能是实体类，或者List类型 |
|keyDataType| DataTypeEnum|LIST,ENTITY|LIST|你将告诉插件你封装在key值的数据类型是List，还是实体类型|



  ##### 2、@Replace

|  参数   | 类型  | 可选值  | 默认值 | 说明  |
|  ----   | ---- |  ----   | ---- | ----   |
| rulePath  | Class | BankCardRuleImpl.class<br />IdentityCardRuleImpl.class<br />NameRuleImpl.class<br />VehiclePlateRuleImpl.class  |NameRuleImpl.class| 你需要提供该字段的替换规则 |
| description  | String | -- |-- | 对注解的描述（可选） |



  ##### 3、@ChildReplace 若你需要替换实体中的嵌套数据，则需要该注解

|  参数   | 类型  | 可选值  | 默认值 | 说明  |
|  ----   | ---- |  ----   | ---- | ----   |
|keyDataType| DataTypeEnum|LIST,ENTITY|LIST| 你需要提供替换字段值的类型 |

