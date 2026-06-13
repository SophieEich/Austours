package MasterTable.dao;

import MasterTable.entity.user.UsersHibernate;
import MasterTable.util.HibernateUtil;
import org.hibernate.Session;

public class UserDAO {

    public UsersHibernate findByUsernameAndPassword(String username, String password) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "FROM UsersHibernate uh WHERE uh.username = :u AND uh.password = :p",
                            UsersHibernate.class)
                    .setParameter("u", username)
                    .setParameter("p", password)
                    .uniqueResult();
        }
    }
}