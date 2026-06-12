package ru.itmo.web.lab3.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@ApplicationScoped
@Getter
public class MBeansConfig {
    @Inject
    private PointsCounter pointsCounter;
    @Inject
    private AverageClickTime averageClickTime;
    private ObjectName pointsCounterName;
    private ObjectName averageClickTimeName;

    public void registerMBeans() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

            pointsCounterName = new ObjectName("ru.itmo.web:type=PointsCounter");
            averageClickTimeName = new ObjectName("ru.itmo.web:type=AverageClickTime");

            pointsCounter.setObjectName(pointsCounterName);
            averageClickTime.setObjectName(averageClickTimeName);

            mBeanServer.registerMBean(pointsCounter, pointsCounterName);
            mBeanServer.registerMBean(averageClickTime, averageClickTimeName);

            averageClickTime.setPointsCounterObjectName(pointsCounterName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterMBeans() {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            if (mBeanServer.isRegistered(pointsCounterName)) {
                mBeanServer.unregisterMBean(pointsCounterName);
            }
            if (mBeanServer.isRegistered(averageClickTimeName)) {
                mBeanServer.unregisterMBean(averageClickTimeName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
