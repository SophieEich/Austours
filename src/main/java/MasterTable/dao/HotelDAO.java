package MasterTable.dao;

import MasterTable.entity.Hotel;
import MasterTable.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

import javax.swing.*;

public class HotelDAO {

    // US- 4
    //Needs to be updated for the SQL Server, then with selects and creates, updates, inserts
    public Hotel addHotel(Hotel h) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            s.persist(h); // nach persist() hat h.getId() den Wert von der DB
            tx.commit();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not save: " + e.getMessage());
        }
        return h; // ID ist jetzt gesetzt
    }

    public void updateHotel(Hotel h) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            s.merge(h);
            tx.commit();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not update: " + e.getMessage());
        }
    }

    public void deleteHotel(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Hotel h = s.get(Hotel.class, id);
            if (h != null) {
                s.remove(h);
            }
            tx.commit();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not delete: " + e.getMessage());
        }
    }

        // US24: Only loads hotels owned by the logged-in hotel representative.
        //Filtered using the foreign key `representative_id` (= user ID).
        public List<Hotel> getHotelsForRepresentative(Long representativeId){
            try (Session s = HibernateUtil.getSessionFactory().openSession()) {
                return s.createQuery(
                                "FROM Hotel h WHERE h.representative.id = :rid", Hotel.class)
                        .setParameter("rid", representativeId)
                        .list();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Could not load hotels: " + e.getMessage());
                return java.util.Collections.emptyList();
            }

    }


}
