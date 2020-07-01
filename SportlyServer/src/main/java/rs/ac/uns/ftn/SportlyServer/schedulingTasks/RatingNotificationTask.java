package rs.ac.uns.ftn.SportlyServer.schedulingTasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.SportlyServer.dto.PushNotificationRequest;
import rs.ac.uns.ftn.SportlyServer.dto.RatingSchedulerEnum;
import rs.ac.uns.ftn.SportlyServer.model.Event;
import rs.ac.uns.ftn.SportlyServer.model.Participation;
import rs.ac.uns.ftn.SportlyServer.model.SportsField;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.service.EventService;
import rs.ac.uns.ftn.SportlyServer.service.PushNotificationService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RatingNotificationTask {

    @Autowired
    EventService eventService;

    @Autowired
    PushNotificationService pushNotificationService;

    @Scheduled(fixedRate = 60000)   //na 1 min (60000)
    public void checkRatings() {
        getCurrentTimeUsingDate();
        List<Event> events = eventService.getAllEventsByRatingScheduler(RatingSchedulerEnum.ENDED);

        for(Event event : events){
            pushData(event);
        }

    }

    public void pushData(Event event){
        SportsField sportsField = event.getSportsField();
        for(Participation participation1 : event.getParticipationList()){
            User userToSendData = participation1.getUser();

            Map<String, String> data = new HashMap<>();
            data.put("sportFieldId", sportsField.getId().toString());
            data.put("sportFieldName", sportsField.getName());
            data.put("numbOfParticipants",(event.getParticipationList().size()) + "");

            int index = 0;
            for(Participation participation2 : event.getParticipationList()){
                User userToRate = participation2.getUser();
                if(!userToSendData.getEmail().equals(userToRate.getEmail())){
                    index++;
                    data.put("id" + index, userToRate.getId().toString());
                    data.put("name" + index, userToRate.getFirstName() + " " + userToRate.getLastName());
                }
            }

            //creator
            User creator = event.getCreator();
            index++;
            data.put("id" + index, creator.getId().toString());
            data.put("name" + index, creator.getFirstName() + " " + creator.getLastName());

            data.put("notificationType","RATING_REQUEST"); //RATING_REQUEST
            data.put("title","Rating request");
            data.put("message","Please rate this sport field and all participants.");

            PushNotificationRequest request = new PushNotificationRequest();
            request.setMessage("Please rate this sport field and all participants.");
            request.setTitle("Rating request");
            request.setTopic(userToSendData.getId().toString());   //kome saljem
            pushNotificationService.sendPushNotification(request,data);
        }

        User creator = event.getCreator();
        Map<String, String> data = new HashMap<>();
        data.put("sportFieldId", sportsField.getId().toString());
        data.put("sportFieldName", sportsField.getName());
        data.put("numbOfParticipants",(event.getParticipationList().size()) + "");

        int index = 0;
        for(Participation participation : event.getParticipationList()){
            User userToRate = participation.getUser();
            index++;
            data.put("id" + index, userToRate.getId().toString());
            data.put("name" + index, userToRate.getFirstName() + " " + userToRate.getLastName());
        }

        data.put("notificationType","RATING_REQUEST"); //RATING_REQUEST
        data.put("title","Rating request");
        data.put("message","Please rate this sport field and all participants.");

        PushNotificationRequest request = new PushNotificationRequest();
        request.setMessage("Please rate this sport field and all participants.");
        request.setTitle("Rating request");
        request.setTopic(creator.getId().toString());   //kome saljem
        pushNotificationService.sendPushNotification(request,data);
    }

    public void getCurrentTimeUsingDate() {
        Date date = new Date();
        String strDateFormat = "HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);
        System.out.println("-------------------- " + formattedDate + " --------------------");
    }
}
