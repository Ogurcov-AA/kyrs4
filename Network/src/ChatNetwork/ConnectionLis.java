package ChatNetwork;

import java.io.InputStream;

public interface ConnectionLis {
    void ConnectionReady(Connection connection);
    void ReceiveString(Connection connection, String str, InputStream bytes);
    void Disconnection (Connection connection);
    void Exception(Connection connection, Exception e);
}
