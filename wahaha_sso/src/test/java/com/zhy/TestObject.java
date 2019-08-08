package com.zhy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestObject {

    @Autowired
    private LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    @Test
    public void test01(){
        EntityManager entityManager = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory().createEntityManager();

        Query nativeQueryCount = entityManager.createNativeQuery("SELECT COUNT(*) as amount FROM base_user WHERE 1=1");

        System.out.println(nativeQueryCount.getSingleResult());
    }
}
