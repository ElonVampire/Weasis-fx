package org.weasis.launcher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class WeasisApp extends Application {

    private volatile Stage splashStage = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.splashStage = primaryStage;

        Executors.defaultThreadFactory().newThread(() -> {
            // Thread.currentThread().setContextClassLoader(WeasisLauncher.class.getClassLoader());
            WeasisLauncher.launch(getParameters(), WeasisApp.this);
        }).start();
    }

    public Stage getSplashStage() {
        return splashStage;
    }

    /**
     * Runs the specified {@link Runnable} on the JavaFX application thread and waits for completion.
     *
     * @param action
     *            the {@link Runnable} to run
     * @throws NullPointerException
     *             if {@code action} is {@code null}
     */
    public static void runAndWait(Runnable action) {
        if (action == null) {
            throw new NullPointerException("action");
        }

        // run synchronously on JavaFX thread
        if (Platform.isFxApplicationThread()) {
            try {
                action.run();
            } catch (Throwable t) {
                System.err.println("Exception in JavaFX runnable");
            }
            return;
        }

        // queue on JavaFX thread and wait for completion
        final CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                doneLatch.countDown();
            }
        });

        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            System.err.println("Interruption in JavaFX runAndWait");
        }
    }
}
