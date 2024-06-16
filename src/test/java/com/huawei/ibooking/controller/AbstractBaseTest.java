package com.huawei.ibooking.controller;

/**
 * @author yuqichen
 * @date 2022/5/21 11:20 下午
 */

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.SQLException;

/**
 *  <pre>
 * 单元测试基类
 * 封装DBunit相关操作方法
 * </pre>
 * https://www.cnblogs.com/wade-xu/p/4547381.html
 * @author oyf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
//继承AbstractTransactionalJUnit4SpringContextTests会在方法执行完成后进行事务回滚，如果需要不回滚事务需要在方法上加上 @Rollbak(false)
public abstract class AbstractBaseTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private DataSource dataSource;
    /**
     * 数据库连接对象
     */
    private static IDatabaseConnection conn;
    /**
     * 备份文件
     */
    private File tempFile;
    /**
     * 文件跟目录
     */
    public static final String ROOT_URL = "src/test/resources/";

//    DBunit方法  --------------------------------------------------------

    /**
     * 获取数据库连接
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        //get DataBaseSourceConnection
        conn = new DatabaseConnection(DataSourceUtils.getConnection(dataSource));
    }

    /**
     * 关闭数据库连接
     * @throws Exception
     */
    //这里不能用@After，当测试方法有@Rollback(false)注解时会在事务没有结束之前关闭了数据库连接
    @AfterTransaction
    public void teardown() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * Get Query DataSet
     *
     * @Title: getQueryDataSet
     * @return
     * @throws SQLException
     */
    protected QueryDataSet getQueryDataSet() throws SQLException {
        return new QueryDataSet(conn);
    }


    /**
     * 备份表数据
     *
     * @Title: backupCustom
     * @param tableName
     * @throws Exception
     */
    protected void backupCustom(String... tableName){
        try {
            // back up specific files
            QueryDataSet qds = getQueryDataSet();
            for (String str : tableName) {

                qds.addTable(str);
            }
            tempFile = new File(ROOT_URL+"temp.xml");
            FlatXmlDataSet.write(qds, new FileWriter(tempFile), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 清空表数据，并导入测试数据
     * @throws Exception
     */
    public void importTables(String file){
        try {
            IDataSet dataSet = new FlatXmlDataSetBuilder().build(new File(ROOT_URL+file));
            DatabaseOperation.CLEAN_INSERT.execute(conn, dataSet);
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回滚数据
     *
     * @Title: rollback
     * @throws Exception
     */
    protected void rollback(){
        try {
            // get the temp file
            FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
            builder.setColumnSensing(true);
            IDataSet ds =builder.build(new FileInputStream(tempFile));

            // recover database
            DatabaseOperation.CLEAN_INSERT.execute(conn, ds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空表数据
     *
     * @param tableName
     * @throws Exception
     */
    protected void clearTable(String tableName){
        try {
            DefaultDataSet dataset = new DefaultDataSet();
            dataset.addTable(new DefaultTable(tableName));
            DatabaseOperation.DELETE_ALL.execute(conn, dataset);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
