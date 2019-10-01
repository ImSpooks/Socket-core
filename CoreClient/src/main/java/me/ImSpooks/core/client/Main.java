package me.ImSpooks.core.client;

import me.ImSpooks.core.common.client.Client;

/**
 * Created by Nick on 26 sep. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class Main {

    public Main() {
        Client client = new Client("127.0.0.1", 7000, "Test Client");
    }
}
