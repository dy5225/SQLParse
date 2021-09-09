package SQLParseMethod;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SQLParsePlus {
    public void ParsePlus(){
        String sql ="update bbs_topic_info\n" +
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

        String data = null;
        String data2 = null;

        DbType dbType = JdbcConstants.ORACLE;
        List<SQLStatement> stmtList = null;
        try {
            stmtList = SQLUtils.parseStatements(sql, dbType);
        } catch (Exception e) {

        }
        //解析出的独立语句的个数
        for (int i = 0; i < stmtList.size(); i++) {
            SQLStatement stmt = stmtList.get(i);
            OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
            stmt.accept(visitor);
            //获取操作方法名称,依赖于表名称

            System.out.println("tableName : " + visitor.getTables().toString());
            Map<TableStat.Name, TableStat> tables = visitor.getTables();
            for (TableStat.Name key : tables.keySet()) {
                if (data == null) {
                    data = "tableName=" + key;
                } else {
                    data = data + "," + key;
                }
            }
            //获取字段名称
            System.out.println("fields : " + visitor.getColumns());
            Collection<TableStat.Column> columns = visitor.getColumns();
            for (int j = 0; j < columns.toArray().length; j++){
                Object o = columns.toArray()[j];
                String[] split1 = o.toString().split("\\.");
//                System.out.println(split[1]);
                if (data2 == null) {
                    data2 = "&dbType=" + split1[1];
                } else {
                    data2 = data2 + "," + split1[1];
                }
            }
            data = data + data2;
            System.out.println(data);
        }
    }
}
