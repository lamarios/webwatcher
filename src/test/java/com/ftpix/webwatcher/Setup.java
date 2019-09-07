package com.ftpix.webwatcher;

import com.ftpix.sparknnotation.Sparknotation;
import com.ftpix.webwatcher.server.TestWebController;
import spark.Spark;

import java.io.IOException;

public class Setup {
    private static boolean setUp = false;

    public static void setUp() throws IOException {
        if (!setUp) {
            Sparknotation.init();
            setUp = true;

            // the server might not be ready on time
            Spark.awaitInitialization();
        }
    }

    public static void stop() {
        Spark.stop();
    }
}
