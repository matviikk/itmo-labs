package utility;

import model.Difficulty;
import model.LabWork;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

public class LabWorkCollectionManager {

    private final TreeSet<LabWork> collection = new TreeSet<>();
    private final Collection<LabWork> syncCollection = Collections.synchronizedCollection(collection);

    public Collection<LabWork> getSyncCollection() {
        return syncCollection;
    }

    private final LabWorkDAO labWorkDAO;
    private final UserDAO userDAO;

    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    public void setLastSaveTime(LocalDateTime lastSaveTime) {
        this.lastSaveTime = lastSaveTime;
    }

    private LocalDateTime lastSaveTime;

    public LabWorkCollectionManager(){
        lastSaveTime = null;
        this.labWorkDAO = new LabWorkDAO();
        this.userDAO = new UserDAO();
        loadCollection();
    }

    public Integer add(LabWork labWork, int userID) {
        int newID = labWorkDAO.addLabWork(labWork, userID);
        if (newID < 0) return -1;
        labWork.setId(newID);
        labWork.setUserId(userID);
        syncCollection.add(labWork);
        return newID;

    }

    public boolean update(LabWork labWork, int userId) {
        System.out.println(labWork);
        System.out.println(userId);
        if (!contains(labWork)) {
            return false;
        }
        //if (!labWorkDAO.removeLabWorkById(labWork.getId())) return false;
        if (!labWorkDAO.updateLabWork(labWork, userId)) return false;
        syncCollection.remove(getFirstById(labWork.getId()));
        labWork.setUserId(userId);
        syncCollection.add(labWork);
        return true;
    }

    public LabWork getFirstById(int id) {
        for (LabWork o : syncCollection) {
            if (o.getId() == id) {
                return o;
            }
        }
        return null;
    }
    public boolean contains(LabWork labWork) {
        for (LabWork o : syncCollection) {
            if (o.getId() == labWork.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean loadCollection() {
        Collection<LabWork> loadedTickets = labWorkDAO.getAllLabWorks();
        syncCollection.clear();
        boolean success = syncCollection.addAll(loadedTickets);
        if (success) {
            System.out.println("LabWorks added");
        }
        return true;
    }

//    public boolean removeByUserId(int userId) {
//        boolean result = labWorkDAO.removeLabWorkById(userId);
//        if (result) {
//            syncCollection.removeIf(ticket -> ticket.getUserId() == userId);
//        }
//        return result;
//    }


    public boolean remove(LabWork labWork) {
        if (!labWorkDAO.removeLabWorkById(labWork.getId())) return false;
        syncCollection.remove(labWork);
        return true;
    }

    public void clear(int userId) {
        labWorkDAO.removeLabWorksByUserId(userId);
        syncCollection.removeIf(labWork -> labWork.getUserId() == userId);
    }

    public void removeByDifficulty(Difficulty difficulty) {
        boolean result = labWorkDAO.removeLabWorkByDifficulty(difficulty);
        if (result) {
            syncCollection.removeIf(ticket -> ticket.getDifficulty() == difficulty);
        }
    }

    public void removeById(int id) {
        boolean result = labWorkDAO.removeLabWorkById(id);
        if (result) {
            syncCollection.removeIf(labWork -> labWork.getId() == id);
        }
    }

    public void removeGreater(LabWork labWork) {
        boolean result = labWorkDAO.removeGreater(labWork);
        if (result) {
            syncCollection.removeIf(curLabWork -> curLabWork.compareTo(labWork) > 0);
        }
    }

    public void removeLower(LabWork labWork) {
        boolean result = labWorkDAO.removeLower(labWork);
        if (result) {
            syncCollection.removeIf(curLabWork -> curLabWork.compareTo(labWork) < 0);
        }
    }

    public int getUserByUsername(String login) {
        return userDAO.getUserByUsername(login).getId();
    }
}
