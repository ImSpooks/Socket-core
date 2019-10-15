import me.ImSpooks.core.common.json.JSONConfig;
import me.ImSpooks.core.packets.security.InvalidCredentialsException;
import me.ImSpooks.core.packets.security.SecurityEncryption;
import me.ImSpooks.core.packets.security.shared.SharedEncryption;
import me.ImSpooks.core.server.CoreServer;
import org.tinylog.Logger;

/**
 * Created by Nick on 27 sep. 2019.
 * Copyright © ImSpooks
 */
public class Start {

    public static void main(String[] args) {
        JSONConfig encryption = new JSONConfig("config", "security.json");

        encryption.expect("port", 7000);
        encryption.expect("password", "INSERT HERE");
        encryption.expect("encryption_key", "INSERT HERE");
        encryption.expect("init_vector", "INSERT HERE");


        String enKey = encryption.getString("encryption_key");
        String vecKey = encryption.getString("init_vector");

        try {
            SharedEncryption.setEncryption(SecurityEncryption.newInstance(enKey, vecKey));
        } catch (InvalidCredentialsException e) {
            Logger.error(e);
        }

        new CoreServer(
                encryption.getString("password"),
                encryption.getInt("port"));
    }
}
