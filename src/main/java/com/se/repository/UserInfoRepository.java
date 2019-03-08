package com.se.repository;

import com.se.model.UserInfo;
import com.se.model.UserProfile;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import javax.persistence.criteria.*;
import org.hibernate.*;
import org.hibernate.query.Query;

import java.util.*;

@Service
public class UserInfoRepository {

    Map<Integer, UserInfo> userInfoMap = new HashMap<>();

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // encrypt?
    public UserInfo save(UserInfo user) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        List<UserInfo> result = null;

        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return user;//??
        /*user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userInfoMap.put(user.getId(), user);*/
    }

    public List<UserInfo> allUsers() {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        List<UserInfo> result = null;

        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<UserInfo> query = builder.createQuery(UserInfo.class);
            Root<UserInfo> root = query.from(UserInfo.class);
            query.select(root);
            Query<UserInfo> q = session.createQuery(query);
            result = q.list();
            for(UserInfo u: result){
                System.out.println(u.getId()+" "+u.getUsername()+" "+u.getPassword());
            }
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return result;
        //return new ArrayList<UserInfo>(userInfoMap.values());
    }


    public Optional<UserInfo> findByUsername(final String username) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Optional<UserInfo> opt = null;

        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<UserInfo> query = builder.createQuery(UserInfo.class);
            Root<UserInfo> root = query.from(UserInfo.class);
            query.select(root).where(builder.equal(root.get("username"), username));
            Query<UserInfo> q = session.createQuery(query);
            //avoid exception, set max results as 1
            UserInfo user = q.setMaxResults(1).getSingleResult();
            System.out.println(user.getId()+user.getUsername()+user.getPassword());
            opt = Optional.of(user);
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return opt;
    }

    public static void main(String[] args){
        System.out.println("print user info");
        UserInfoRepository u = new UserInfoRepository();
        UserInfo user = UserInfo.builder().username("Chuyi").password("woshidalao").build();
        u.save(user);
        Optional<UserInfo> opt = u.findByUsername("Chuyi");
        u.allUsers();
    }

}