package MasterTable.dao;

import MasterTable.entity.user.UsersHibernate;
import MasterTable.util.HibernateUtil;
import org.hibernate.Session;
import java.security.MessageDigest;

public class UserDAO {

    public UsersHibernate findByUsernameAndPassword(String username, String password) {
        String hashedPassword = hashPassword(password);
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "FROM UsersHibernate uh WHERE uh.username = :u AND uh.password = :p", UsersHibernate.class)
                    .setParameter("u", username)
                    .setParameter("p", hashedPassword)
                    .uniqueResult();
        }catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return null;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            String result = sb.toString();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    }



