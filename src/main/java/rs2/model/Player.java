package rs2.model;

import rs2.net.Client;
import rs2.net.ClientPacketWriter;

/**
 * @author Patrick
 */
public final class Player {

    private final ClientPacketWriter writer = new ClientPacketWriter(this);

    private Position position = new Position(3200, 3200, 0);

    private final Client client;
    private final String username;
    private final String password;

    public Player(Client client, String username, String password) {
        this.client = client;
        this.username = username;
        this.password = password;
    }

    public ClientPacketWriter getWriter() {
        return writer;
    }

    public Client getClient() {
        return client;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Player{" +
                "client=" + client +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
