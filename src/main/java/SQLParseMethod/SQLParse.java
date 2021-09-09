package SQLParseMethod;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;
import java.util.Map;

public class SQLParse {

    public void Base() {
        // String sql = "update t set name = 'x' where id < 100 limit 10";
// String sql = "SELECT ID, NAME, AGE FROM USER WHERE ID = ? limit 2";
// String sql = "select * from tablename limit 10";

        //            String sql = "delete from tb_issue_list where id='1' and name = 'zhangsa'";
        String sql2 = "delete from tb_issue_list where id='1' and name = 'zhangsa' and sex = 'nan'";
        //        String sql = "select name, id, sex from tb_issue_list where number = '2'";
        String sql = "update bbs_topic_info\n" +
                "set topic_rep_num = (select nvl(topic_rep_num,0) as topic_rep_num\n" +
                "                     from (select t1.topic_id,r1.topic_rep_num\n" +
                "                           from bbs_topic_info t1\n" +
                "                           left join (select r.topic_id,count(r.topic_rep_id) as  topic_rep_num\n" +
                "                                      from bbs_topic_rep_info r,bbs_topic_info t\n" +
                "                                      where r.topic_id = t.topic_id and r.topic_rep_status = 1\n" +
                "                                      group by r.topic_id\n" +
                "                                      )r1\n" +
                "                           on r1.topic_id = t1.topic_id\n" +
                "                           )ray\n" +
                "                      where ray.topic_id = bbs_topic_info.topic_id\n" +
                "                     ) ";
        DbType dbType = JdbcConstants.ORACLE;

        //格式化输出
//            String result = SQLUtils.format(sql, dbType);
//            System.out.println(result); // 缺省大写格式
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

//解析出的独立语句的个数
//            System.out.println("size is:" + stmtList.size());
        for (int i = 0; i < stmtList.size(); i++) {

            SQLStatement stmt = stmtList.get(i);
            OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
            stmt.accept(visitor);
//                System.out.println("=="+ visitor);
//获取表名称
//                System.out.println("Tables : " + visitor.getCurrentTable());
//获取操作方法名称,依赖于表名称
            System.out.println("Manipulation : " + visitor.getTables());
            Map<TableStat.Name, TableStat> tables = visitor.getTables();
            for (TableStat.Name key : tables.keySet()) {
                System.out.println("key:" + key + " " + "Value:" + tables.get(key));
            }
            System.out.println(tables.keySet());
//获取字段名称
            System.out.println("fields : " + visitor.getColumns());
//获取排序名称
            System.out.println("getOrderByColumns : " + visitor.getOrderByColumns());
//获取分组
            System.out.println("getGroupByColumns : " + visitor.getGroupByColumns());
        }
    }
}
