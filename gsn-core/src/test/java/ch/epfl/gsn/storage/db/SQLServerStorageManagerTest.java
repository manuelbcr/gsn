package ch.epfl.gsn.storage.db;

import static org.junit.Assert.*;
import org.junit.Test;
import java.sql.*;
import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;


public class SQLServerStorageManagerTest {
    @Test
    public void testSQLServerStorageManager() throws SQLException{
        SQLServerStorageManager sm= new SQLServerStorageManager();
        assertEquals("jdbc:jtds:sqlserver:",sm.getJDBCPrefix());

        assertEquals("DROP INDEX #NAME ON #TABLE",sm.getStatementDropIndex());
        assertEquals("DROP VIEW #NAME",sm.getStatementDropView());
        assertEquals(208,sm.getTableNotExistsErrNo());
        assertEquals("select * FROM xy LIMIT 10 OFFSET 10",sm.addLimit("select * FROM xy",10,10));
        assertEquals("select convert(bigint,datediff(second,'1/1/1970',current_timestamp))*1000 ", sm.getStatementDifferenceTimeInMillis());
        assertEquals("Drop table xy", sm.getStatementDropTable("xy",null).toString());

        DataField[] fields = new DataField[]{
            new DataField("value", DataTypes.INTEGER),
            new DataField("value1", DataTypes.INTEGER)
        };

        assertEquals("CREATE TABLE xy (PK BIGINT NOT NULL IDENTITY, timed BIGINT NOT NULL, VALUE Integer ,VALUE1 Integer)",sm.getStatementCreateTable("xy",fields).toString());
        assertEquals("delete from xy where xy.timed < (select min(timed) from (select top 10 * from xy order by xy.timed DESC ) as x ) ",sm.getStatementUselessDataRemoval("xy",10L).toString());
        assertEquals("delete from xy where xy.timed < (select min(timed) from (select top 10 * from xy order by xy.timed DESC ) as x ) ",sm.getStatementRemoveUselessDataCountBased("xy",10L).toString());
        
        assertEquals("Char",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.CHAR)));
        assertEquals("Binary",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.BINARY)));
        assertEquals("Double",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.DOUBLE)));
        assertEquals("REAL",sm.convertGSNTypeToLocalType(new DataField("value", DataTypes.FLOAT)));
        assertEquals(DataTypes.BIGINT,sm.convertLocalTypeToGSN(Types.BIGINT,1));
        assertEquals(DataTypes.INTEGER,sm.convertLocalTypeToGSN(Types.INTEGER,1));
        assertEquals(DataTypes.SMALLINT,sm.convertLocalTypeToGSN(Types.SMALLINT,1));
        assertEquals(DataTypes.TINYINT,sm.convertLocalTypeToGSN(Types.TINYINT,1));
        assertEquals(DataTypes.VARCHAR,sm.convertLocalTypeToGSN(Types.VARCHAR,1));
        assertEquals(DataTypes.CHAR,sm.convertLocalTypeToGSN(Types.CHAR,1));
        assertEquals(DataTypes.FLOAT,sm.convertLocalTypeToGSN(Types.REAL,1));
        assertEquals(-100,sm.convertLocalTypeToGSN(Types.FLOAT,1));
        assertEquals(DataTypes.DOUBLE,sm.convertLocalTypeToGSN(Types.DOUBLE,1));
        assertEquals(DataTypes.BINARY,sm.convertLocalTypeToGSN(Types.BLOB,1));
    }
}