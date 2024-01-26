package ch.epfl.gsn.storage.db;

import static org.junit.Assert.*;
import org.junit.Test;
import java.sql.*;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;


public class OracleStorageManagerTest {

    @Test
    public void testSQLServerStorageManager() throws SQLException{
        OracleStorageManager sm= new OracleStorageManager();
        assertEquals("jdbc:oracle:thin:",sm.getJDBCPrefix());

        assertEquals("DROP INDEX #NAME ON #TABLE",sm.getStatementDropIndex());
        assertEquals("DROP VIEW #NAME",sm.getStatementDropView());
        assertEquals(208,sm.getTableNotExistsErrNo());
        assertEquals("select * FROM xy WHERE  ROWNUM BETWEEN 10 AND 20 ", sm.addLimit("select * FROM xy",10,10));
        assertEquals("select * FROM xy WHERE  ROWNUM <= 10",sm.addLimit("select * FROM xy",10,0));
        assertEquals("select * FROM xy GROUPBY field1 WHERE  ROWNUM BETWEEN 10 AND 20 ",sm.addLimit("select * FROM xy GROUPBY field1",10,10));
        assertEquals("select * FROM xy WHERE  ROWNUM BETWEEN 10 AND 20  AND (field1 >1)",sm.addLimit("select * FROM xy WHERE field1 >1",10,10));
        assertEquals("select * FROM xy WHERE  ROWNUM BETWEEN 10 AND 20  ORDER BY field1",sm.addLimit("select * FROM xy ORDER BY field1",10,10));
        assertEquals("xyb",sm.tableNamePostFixAppender("xy","b"));
        assertEquals("xy",sm.tableNameGeneratorInString("xy").toString());
        assertEquals("x_y",sm.tableNameGeneratorInString("x_y").toString());
        assertEquals("", sm.getStatementDifferenceTimeInMillis());
        assertEquals("Drop table xy", sm.getStatementDropTable("xy",null).toString());

        DataField[] fields = new DataField[]{
            new DataField("value", DataTypes.INTEGER),
            new DataField("value1", DataTypes.INTEGER)
        };
        assertEquals("CREATE TABLE xy (PK number(38) PRIMARY KEY, timed number(38) NOT NULL, VALUE number(38,0) ,VALUE1 number(38,0))",sm.getStatementCreateTable("xy",fields).toString());
        assertEquals("delete from xy where timed <= ( SELECT * FROM ( SELECT timed FROM xy group by timed ORDER BY timed DESC) where rownum = 11 )",sm.getStatementUselessDataRemoval("xy",10L).toString());
        assertEquals("delete from xy where timed <= ( SELECT * FROM ( SELECT timed FROM xy group by timed ORDER BY timed DESC) where rownum = 11 )",sm.getStatementRemoveUselessDataCountBased("xy",10L).toString());
        
        assertEquals("char",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.CHAR)));
        assertEquals("LONG RAW",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.BINARY)));
        assertEquals("number(38,0)",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.INTEGER)));
        assertEquals("number(38,16)",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.DOUBLE)));
        assertEquals("number(38,8)",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.FLOAT)));
        assertEquals(DataTypes.BIGINT,sm.convertLocalTypeToGSN(Types.NUMERIC,0));
        assertEquals(DataTypes.DOUBLE,sm.convertLocalTypeToGSN(Types.NUMERIC,16));
        assertEquals(DataTypes.FLOAT,sm.convertLocalTypeToGSN(Types.NUMERIC,8));
        assertEquals(DataTypes.VARCHAR,sm.convertLocalTypeToGSN(Types.VARCHAR,1));
        assertEquals(DataTypes.CHAR,sm.convertLocalTypeToGSN(Types.CHAR,1));
        assertEquals(DataTypes.BINARY,sm.convertLocalTypeToGSN(Types.BLOB,1));
        assertEquals(-100,sm.convertLocalTypeToGSN(Types.NULL,1));
       
       
    }


}