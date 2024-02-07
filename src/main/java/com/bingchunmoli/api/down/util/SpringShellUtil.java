package com.bingchunmoli.api.down.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.log.LogMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@Slf4j
public class SpringShellUtil {

    private static final String USR_LOCAL_BIN = "/usr/local/bin";

    private static final boolean MAC_OS = System.getProperty("os.name").toLowerCase().contains("mac");

    private final File workingDirectory;

    /**
     * Create a new {@link SpringShellUtil} instance.
     */
    SpringShellUtil() {
        this(null);
    }

    /**
     * Create a new {@link SpringShellUtil} instance.
     *
     * @param workingDirectory the working directory for the process
     */
    SpringShellUtil(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Runs the given {@code command}. If the process exits with an error code other than
     * zero, an {@link ProcessExitException} will be thrown.
     *
     * @param command the command to run
     * @return the output of the command
     */
    String run(String... command) {
        return run(null, command);
    }

    /**
     * Runs the given {@code command}. If the process exits with an error code other than
     * zero, an {@link ProcessExitException} will be thrown.
     *
     * @param outputConsumer consumer used to accept output one line at a time
     * @param command        the command to run
     * @return the output of the command
     */
    String run(Consumer<String> outputConsumer, String... command) {
        log.trace(String.valueOf(LogMessage.of(() -> "Running '%s'".formatted(String.join(" ", command)))));
        Process process = startProcess(command);
        ReaderThread stdOutReader = new ReaderThread(process.getInputStream(), "stdout", outputConsumer);
        ReaderThread stdErrReader = new ReaderThread(process.getErrorStream(), "stderr", outputConsumer);
        log.trace("Waiting for process exit");
        int exitCode = waitForProcess(process);
        log.trace(String.valueOf(LogMessage.format("Process exited with exit code %d", exitCode)));
        String stdOut = stdOutReader.toString();
        String stdErr = stdErrReader.toString();
        if (exitCode != 0) {
            throw new RuntimeException("new ProcessExitException(exitCode, command, stdOut, stdErr);");
        }
        return stdOut;
    }

    private Process startProcess(String[] command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(this.workingDirectory);
        try {
            return processBuilder.start();
        } catch (IOException ex) {
            String path = processBuilder.environment().get("PATH");
            if (MAC_OS && path != null && !path.contains(USR_LOCAL_BIN)
                    && !command[0].startsWith(USR_LOCAL_BIN + "/")) {
                String[] localCommand = command.clone();
                localCommand[0] = USR_LOCAL_BIN + "/" + localCommand[0];
                return startProcess(localCommand);
            }
            throw new RuntimeException("new ProcessStartException(command, ex);");
        }
    }

    private int waitForProcess(Process process) {
        try {
            return process.waitFor();
        } catch (InterruptedException ex) {
            throw new IllegalStateException("Interrupted waiting for %s".formatted(process));
        }
    }

    /**
     * Thread used to read stream input from the process.
     */
    private static class ReaderThread extends Thread {

        private final InputStream source;

        private final Consumer<String> outputConsumer;

        private final StringBuilder output = new StringBuilder();

        private final CountDownLatch latch = new CountDownLatch(1);

        ReaderThread(InputStream source, String name, Consumer<String> outputConsumer) {
            this.source = source;
            this.outputConsumer = outputConsumer;
            setName("OutputReader-" + name);
            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(this.source, StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                while (line != null) {
                    this.output.append(line);
                    this.output.append("\n");
                    if (this.outputConsumer != null) {
                        this.outputConsumer.accept(line);
                    }
                    line = reader.readLine();
                }
                this.latch.countDown();
            } catch (IOException ex) {
                throw new UncheckedIOException("Failed to read process stream", ex);
            }
        }

        @Override
        public String toString() {
            try {
                this.latch.await();
                return this.output.toString();
            } catch (InterruptedException ex) {
                return null;
            }
        }

    }

}
