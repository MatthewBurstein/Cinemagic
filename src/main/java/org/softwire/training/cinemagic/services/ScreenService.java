package org.softwire.training.cinemagic.services;

import org.softwire.training.cinemagic.models.Screen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@Transactional
public class ScreenService {

    private EntityManager entityManager;

    public Screen create(Screen screen) {
        entityManager.persist(screen);
        entityManager.refresh(screen);
        return screen;
    }

    public void delete(Integer id) {
        Screen screen = entityManager.find(Screen.class, id);
        if (screen != null) {
            entityManager.remove(screen);
        }
    }
}
