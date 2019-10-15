import me.ImSpooks.core.bridge.Bridge;
import me.ImSpooks.core.client.CoreClient;
import me.ImSpooks.core.common.json.JSONConfig;
import me.ImSpooks.core.packets.security.InvalidCredentialsException;
import me.ImSpooks.core.packets.security.SecurityEncryption;
import me.ImSpooks.core.packets.security.shared.SharedEncryption;
import org.tinylog.Logger;

/**
 * Created by Nick on 13 Oct 2019.
 * Copyright © ImSpooks
 */
public class Start {

    public static void main(String[] args) {
        {
            JSONConfig clientDetails = new JSONConfig("config", "client.json");

            clientDetails.expect("bridge_port", 7001);

            clientDetails.expect("server_ip", "127.0.0.1");
            clientDetails.expect("server_port", 7000);
            clientDetails.expect("client_name", "defaultClient");

            for (String d : new String[]{"password", "encryption_key", "init_vector"}) {
                clientDetails.expect(d, "INSERT");
            }

            try {
                SharedEncryption.setEncryption(SecurityEncryption.newInstance(
                        clientDetails.getString("encryption_key"),
                        clientDetails.getString("init_vector")));
            } catch (InvalidCredentialsException e) {
                Logger.error(e);
            }

            new Bridge(new CoreClient(
                    clientDetails.getString("server_ip"),
                    clientDetails.getInt("server_port"),
                    clientDetails.getString("password"),
                    clientDetails.getString("client_name")
            ));
        }
    }

}