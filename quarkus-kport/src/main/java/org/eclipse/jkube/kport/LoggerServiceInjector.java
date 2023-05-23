package org.eclipse.jkube.kport;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import org.jboss.logging.Logger;
import org.eclipse.jkube.kit.common.KitLogger;

@Dependent
public class LoggerServiceInjector {

    private static final Logger QLOGGER = Logger.getLogger("kube-kport");

    KitLogger logger = new KitLogger() {

        @Override
        public void debug(String format, Object... params) {
            QLOGGER.debugf(format, params);
        }

        @Override
        public void info(String format, Object... params) {
            QLOGGER.infof(format, params);
        }

        @Override
        public void warn(String format, Object... params) {
            QLOGGER.warnf(format, params);
        }

        @Override
        public void error(String format, Object... params) {
            QLOGGER.errorf(format, params);
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

    };

    @Produces
    public KitLogger logger() {
        return logger;
    }
}