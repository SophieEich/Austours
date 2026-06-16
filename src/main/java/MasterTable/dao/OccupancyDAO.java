package MasterTable.dao;
import MasterTable.entity.Hotel;
import MasterTable.entity.Occupancy;
import MasterTable.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
public class OccupancyDAO {

    public List<Occupancy> getAllOccupancies() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "SELECT o FROM Occupancy o JOIN FETCH o.hotel", Occupancy.class
            ).list();
        } catch (Exception e) {
            System.out.println("Could not load occupancies: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public List<Hotel> getAllHotels() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from Hotel", Hotel.class).list();
        } catch (Exception e) {
            System.out.println("Could not load hotels: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public void saveOccupancy(Occupancy occ) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            s.persist(occ);
            tx.commit();
        } catch (Exception e) {
            System.out.println("Could not save occupancy: " + e.getMessage());
        }
    }

    //US-23
    public void updateOccupancy(Occupancy occ) {
        Transaction tx = null;

        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();

            s.merge(occ);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.out.println("Could not update occupancy: " + e.getMessage());
        }
    }




    // US-24
    public List<Occupancy> getOccupanciesForRepresentative(String username) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("""
            SELECT o
            FROM Occupancy o
            JOIN FETCH o.hotel h
            JOIN h.representative r
            WHERE r.username = :username
        """, Occupancy.class)
                    .setParameter("username", username)
                    .list();
        }
    }



}
