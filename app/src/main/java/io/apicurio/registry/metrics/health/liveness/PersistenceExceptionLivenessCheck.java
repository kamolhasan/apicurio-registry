package io.apicurio.registry.metrics.health.liveness;

import io.apicurio.common.apps.config.Info;
import io.apicurio.registry.metrics.health.AbstractErrorCounterHealthCheck;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.slf4j.Logger;

import static io.apicurio.common.apps.config.ConfigPropertyCategory.CATEGORY_HEALTH;

/**
 * Fail liveness check if the number of exceptions thrown by artifactStore is too high.
 */
@ApplicationScoped
@Liveness
@Default
public class PersistenceExceptionLivenessCheck extends AbstractErrorCounterHealthCheck
        implements HealthCheck, LivenessCheck {

    @Inject
    Logger log;

    /**
     * Maximum number of exceptions raised by artifactStore implementation, as captured by this interceptor,
     * before the liveness check fails.
     */
    @ConfigProperty(name = "apicurio.metrics.persistence-exception-liveness-check.error-threshold", defaultValue = "1")
    @Info(category = CATEGORY_HEALTH, description = "Error threshold of persistence liveness check", availableSince = "1.0.2.Final")
    Integer configErrorThreshold;

    /**
     * The counter is reset after some time without errors. i.e. to fail the check after 2 errors in a minute,
     * set the threshold to 1 and this configuration option to 60. TODO report the absolute count as a metric?
     */
    @ConfigProperty(name = "apicurio.metrics.persistence-exception-liveness-check.counter-reset-window-duration.seconds", defaultValue = "60")
    @Info(category = CATEGORY_HEALTH, description = "Counter reset window duration of persistence liveness check", availableSince = "1.0.2.Final")
    Integer configCounterResetWindowDurationSec;

    /**
     * If set to a positive value, reset the liveness status after this time window passes without any further
     * errors.
     */
    @ConfigProperty(name = "apicurio.metrics.persistence-exception-liveness-check.status-reset-window-duration.seconds", defaultValue = "300")
    @Info(category = CATEGORY_HEALTH, description = "Status reset window duration of persistence liveness check", availableSince = "1.0.2.Final")
    Integer configStatusResetWindowDurationSec;

    @ConfigProperty(name = "apicurio.metrics.persistence-exception-liveness-check.logging.disabled", defaultValue = "false")
    @Info(category = CATEGORY_HEALTH, description = "Disable logging of persistence liveness check", availableSince = "2.0.0.Final")
    Boolean disableLogging;

    @PostConstruct
    void init() {
        init(configErrorThreshold, configCounterResetWindowDurationSec, configStatusResetWindowDurationSec);
    }

    @Override
    public synchronized HealthCheckResponse call() {
        callSuper();
        return HealthCheckResponse.builder().name("PersistenceExceptionLivenessCheck")
                .withData("errorCount", errorCounter).status(up).build();
    }

    @Override
    public void suspect(String reason) {
        if (disableLogging != Boolean.TRUE) {
            log.warn("Liveness problem suspected in PersistenceExceptionLivenessCheck: {}", reason);
        }
        super.suspectSuper();
        if (disableLogging != Boolean.TRUE) {
            log.info("After this event, the error counter is {} (out of the maximum {} allowed).",
                    errorCounter, configErrorThreshold);
        }
    }

    @Override
    public void suspectWithException(Throwable reason) {
        if (disableLogging != Boolean.TRUE) {
            log.warn(
                    "Liveness problem suspected in PersistenceExceptionLivenessCheck because of an exception: ",
                    reason);
        }
        super.suspectSuper();
        if (disableLogging != Boolean.TRUE) {
            log.info("After this event, the error counter is {} (out of the maximum {} allowed).",
                    errorCounter, configErrorThreshold);
        }
    }
}
