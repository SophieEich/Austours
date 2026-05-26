package MasterTable;

import MasterTable.Login.UsersHibernate;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "off");
        System.setProperty("org.slf4j.simpleLogger.log.org.hibernate", "off");
        System.setProperty("org.slf4j.simpleLogger.log.org.jboss", "off");
    }

    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {

        try {
            return new Configuration()
                    .configure()               // load the config file
                    .addAnnotatedClass(Hotel.class)  // register entity, add more if needed
                    .addAnnotatedClass(Occupancy.class)
                    .addAnnotatedClass(UsersHibernate.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("SessionFactory couldnt be created.");
            throw new ExceptionInInitializerError(ex);
        }
    }

}