package org.example.decoupleConfig;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @author : zhangxiaobo
 * @version : v1.0
 * @description : 一句话描述该类的功能
 * @createTime : 2023/6/24 10:07
 * @updateUser : zhangxiaobo
 * @updateTime : 2023/6/24 10:07
 * @updateRemark : 说明本次修改内容
 */
public class DecoupleConfig {
  public String dataSourceUrl;
  public String username;

  public String password;

  public String userTableName;

  public String useridColumnName;
  public String usernameColumnName;
  public String passwordColumnName;
  public String isbannedColumnName;

  public String isdeletedColumnName;

  private String configFileName = "application.yml";

  private String fixedPath="src/main/resources/application.yml";
  String entityName = "User";
  String daoName = "UserMapper";
  String MapperName = "UserMapper";


  Map<String,String> sqlType2Java = new HashMap<String,String>(){{
    put("CHAR","String");
    put("DATETIME","java.util.Date");
    put("VARCHAR","String");
    put("LONGVARCHAR","String");
    put("NUMERIC","java.math.BigDecimal");
    put("DECIMAL","java.math.BigDecimal");
    put("BIT","boolean");
    put("TINYINT","byte");
    put("SMALLINT","short");
    put("INT","Integer");
    put("INTEGER","Integer");
    put("BIGINT","long");
    put("REAL","float");
    put("FLOAT","double");
    put("DOUBLE","double");
    put("BINARY","byte[]");
    put("VARBINARY","byte[]");
    put("LONGVARBINARY","byte[]");
    put("DATE","java.sql.Date");
    put("TIME","java.sql.Time");
    put("TIMESTAMP","java.sql.Timestamp");
  }};

  public DecoupleConfig(String dataSourceUrl, String username, String password,String userTableName,
                        String usernameColumnName,String passwordColumnName,String isbannedColumnName,
                        String isdeletedColumnName,String useridColumnName
  ) {
    this.dataSourceUrl = dataSourceUrl;
    this.username = username;
    this.password = password;
    this.userTableName = userTableName;
    this.usernameColumnName = usernameColumnName;
    this.passwordColumnName = passwordColumnName;
    this.isbannedColumnName = isbannedColumnName;
    this.isdeletedColumnName = isdeletedColumnName;
    this.useridColumnName = useridColumnName;
  }


  public void init(){
    String src = DecoupleConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//    String src = DecoupleConfig.class.getClassLoader().getResource(configFileName).getPath();
    src = src.split("target")[0] + fixedPath;
    Yaml yaml = new Yaml();
    FileWriter fileWriter;
    FileInputStream fileInputStream;
    try {
      fileInputStream = new FileInputStream(src);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    Map<String,Object> yamlMap = yaml.load(fileInputStream);
    HashMap<String, String> dataSourceVariable = new HashMap<String, String>(){{
      put("usernameColumnName",usernameColumnName);
      put("passwordColumnName",passwordColumnName);
      put("isbannedColumnName",isbannedColumnName);
      put("isdeletedColumnName",isdeletedColumnName);
      put("useridColumnName",useridColumnName);
    }};
    yamlMap.put("dataSourceVariable",dataSourceVariable);
    //获取键并修改
    Map<String,Object> spring = (Map<String, Object>) yamlMap.get("spring");
    Map<String,Object> datasource = (Map<String, Object>) spring.get("datasource");
    if (datasource.get("url").toString().isEmpty()){
      datasource.put("url",dataSourceUrl);
    }
    if (datasource.get("username").toString().isEmpty()){
      datasource.put("username",username);
    }
    if (datasource.get("password").toString().isEmpty()){
      datasource.put("password",password);
    }
    createUserEntityDaoMapper();
    try {
      fileWriter = new FileWriter(src);
      fileWriter.write(yaml.dumpAsMap(yamlMap));
      fileWriter.flush();
      fileWriter.close();
      fileInputStream.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  private void createUserEntityDaoMapper(){
    String src = DecoupleConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//    String src = DecoupleConfig.class.getClassLoader().getResource(configFileName).getPath();
    //设置文件路径
    String mapperPath = src.split("target")[0] + "src/main/resources/mapper/";
    src = src.split("target")[0] + "src/main/java/org/example/";
    String entityPath = src + "entity/";
    String daoPath = src + "dao/";
    StringBuilder entityBuilder = prepareEntityBuilder();
    StringBuilder daoBuilder = prepareDaoBuilder();
    StringBuilder mapperBuilder = prepareMapperBuilder();
    try(Connection connection = DriverManager.getConnection(dataSourceUrl, username, password)) {
      String sql = "select * from "+ userTableName;
      PreparedStatement ps = connection.prepareStatement(sql);
      ResultSet resultSet = ps.executeQuery();
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      for (int i = 1; i <= columnCount; i++) {
        String columnName = metaData.getColumnName(i);
        String camelCase = StrUtil.toCamelCase(columnName);
        String typeName = metaData.getColumnTypeName(i);
        String JDBCType = typeName.equals("INT")? "INTEGER" : typeName.equals("DATETIME")?"TIMESTAMP":typeName;
        //增加entity和mapper的id字段
        if (columnName.equals(useridColumnName)){
          entityBuilder.append("    @TableId(value = \"").append(useridColumnName).append("\", type = IdType.AUTO)\n");
          entityBuilder.append("    private ").append(sqlType2Java.get(typeName))
            .append(" ").append(camelCase).append(";\n\n\n");
          mapperBuilder.append("        <id property=\"").append(camelCase).append("\" column=\"")
            .append(columnName).append("\" jdbcType=\"").append(JDBCType).append("\"/>\n");
        }else {
          //增加entity
          entityBuilder.append("    private ").append(sqlType2Java.get(typeName))
            .append(" ").append(camelCase).append(";\n\n\n");
          //增加mapper
          mapperBuilder.append("        <result property=\"").append(camelCase).append("\" column=\"")
            .append(columnName).append("\" jdbcType=\"").append(JDBCType).append("\"/>\n");
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    entityBuilder.append("    @TableField(exist = false)\n" +
      "    private static final long serialVersionUID = 1L;\n");
    mapperBuilder.append("    </resultMap>\n\n");
    mapperBuilder.append("    <sql id=\"Base_Column_List\">\n");
    mapperBuilder.append("        ").append(useridColumnName+","+usernameColumnName+","+passwordColumnName+",\n")
      .append("        "+isbannedColumnName+","+isdeletedColumnName+"\n"+"    </sql>\n"+"</mapper>");
    entityBuilder.append("}");

    try (FileWriter fileWriter= new FileWriter(entityPath+entityName+".java")){
      fileWriter.write(entityBuilder.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try(FileWriter fileWriter = new FileWriter(daoPath+daoName+".java")) {
      fileWriter.write(daoBuilder.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try(FileWriter fileWriter = new FileWriter(mapperPath+MapperName+".xml")) {
      fileWriter.write(mapperBuilder.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private StringBuilder prepareEntityBuilder(){
    StringBuilder entityBuilder = new StringBuilder();
    entityBuilder.append("package org.example.entity;\n\n");
    entityBuilder.append("import com.baomidou.mybatisplus.annotation.IdType;\n" +
      "import com.baomidou.mybatisplus.annotation.TableField;\n" +
      "import com.baomidou.mybatisplus.annotation.TableId;\n" +
      "import com.baomidou.mybatisplus.annotation.TableName;\n" +
      "import lombok.AllArgsConstructor;\n" +
      "import lombok.Data;\n" +
      "import lombok.NoArgsConstructor;\n" +
      "\n" +
      "import java.io.Serializable;\n" +
      "import java.util.Date;\n\n");
    entityBuilder.append("@TableName(value =\"").append(userTableName).append("\")\n" +
      "@AllArgsConstructor\n" +
      "@NoArgsConstructor\n" +
      "@Data\n");
    entityBuilder.append("public class ").append(entityName).append(" implements Serializable ").append("{\n");
    return entityBuilder;
  }

  private StringBuilder prepareDaoBuilder(){
    StringBuilder daoBuilder = new StringBuilder();
    daoBuilder.append("package org.example.dao;\n\n");
    daoBuilder.append("import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n" +
      "import org.example.entity.User;\n" +
      "import org.apache.ibatis.annotations.Mapper;\n\n");
    daoBuilder.append("@Mapper\n");
    daoBuilder.append("public interface ").append(daoName).append(" extends BaseMapper<User> ").append("{\n");
    daoBuilder.append("}");
    return daoBuilder;
  }

  private StringBuilder prepareMapperBuilder(){
    StringBuilder mapperBuilder = new StringBuilder();
    mapperBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    mapperBuilder.append("<!DOCTYPE mapper\n" +
      "        PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n" +
      "        \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
      "<mapper namespace=\"org.example.dao.UserMapper\">\n\n");
    mapperBuilder.append("    <resultMap id=\"BaseResultMap\" type=\"org.example.entity.User\">\n");
    return mapperBuilder;
  }



//  public static void main(String[] args) {
//    String url = "jdbc:mysql://localhost:3306/explore?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai";
//    String username = "root";
//    String password = "123456";
//    String userTableName = "user";
//    String usernameColumnName = "email";
//    String passwordColumnName = "password";
//    String isbannedColumnName = "is_banned";
//    String isdeletedColumnName = "is_deleted";
//    String useridColumnName = "id";
//    DecoupleConfig decoupleConfig = new DecoupleConfig(url,username,password,userTableName,
//      usernameColumnName,passwordColumnName,isbannedColumnName,isdeletedColumnName,useridColumnName
//      );
//    decoupleConfig.init();
//  }
}
