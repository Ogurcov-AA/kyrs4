package server;

import org.junit.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseTest extends Assert {

    @Test
    public void checkCreateKey(){
        DatabaseHandler databaseHandler = new DatabaseHandler();
        String one = "test";
        String two = "Hisad";
        assertNotEquals(databaseHandler.createNewDialogKey(String.format(one, "UTF-8"), String.format(two, "UTF-8")),null);
    }


    @Test
    public void checkCreateTable(){
        DatabaseHandler databaseHandler = new DatabaseHandler();
            assertTrue(databaseHandler.createDialogKeyTable("testq"));
    }

    @Test
    public void checkkeyDB(){
        DatabaseHandler databaseHandler = new DatabaseHandler();
        assertTrue(databaseHandler.checkKeyOnBD("526935"));
    }

    @Test
    public void checkSearch(){
        DatabaseHandler databaseHandler = new DatabaseHandler();
        String one = "test";
        String two = "hiaad";
        assertNotEquals(databaseHandler.searchDialogKey(String.format(one, "UTF-8")+
                " " +String.format(two, "UTF-8")),null);
    }


    @Test
    public void checkAddMsgInDB(){
        DatabaseHandler databaseHandler = new DatabaseHandler();
        assertTrue(databaseHandler.addMessageToBD("254995 " + "test" + " : adwdwadadawdawdaw dawd aw daw daw daw d"));
    }

    @Test
    public void checkMSGCount () throws SQLException {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        ResultSet resultSet = databaseHandler.countMsgInDialogs("254995 ");
        assertTrue(resultSet.next());
    }

    @Test
    public void getArrayWithNewMsgTest(){
        DatabaseHandler databaseHandler = new DatabaseHandler();
        assertEquals(databaseHandler.getArrayWithNewMsg(1,"254995").size(),0);
    }

}
