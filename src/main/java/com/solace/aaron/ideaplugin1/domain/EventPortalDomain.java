package com.solace.aaron.ideaplugin1.domain;

public class EventPortalDomain {

//    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_INSTANT;
//    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss.AAAZ");
    /**
     *     {
     *             "createdTime": "2022-07-12T20:07:57.808Z",
     *             "updatedTime": "2022-07-12T20:07:57.808Z",
     *             "createdBy": "67tr8tku41",
     *             "changedBy": "67tr8tku41",
     *             "id": "x4oo4skfh5e",
     *             "name": "Aaron Test 1",
     *             "description": "this is a test",
     *             "uniqueTopicAddressEnforcementEnabled": true,
     *             "topicDomainEnforcementEnabled": false,
     *             "type": "applicationDomain"
     *         },
     */
    String tags;
    String link;
    String title;
    String lastActivity;
    String creationDate;
    Integer answerCount;

    public String getCreationDate()
    {
        return this.creationDate;
    }

//    public void setCreationDate(LocalDate creationDate) {
//        this.creationDate = creationDate;
//    }

    public void setCreationDate(String dateString) {
        this.creationDate = dateString;
//        try {
//            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss.AAA'Z");
//            this.creationDate = LocalDate.parse(dateString, dateFormatter);
//        } catch (DateTimeParseException | IllegalArgumentException e) {
//            e.printStackTrace();
//            this.creationDate = LocalDate.ofEpochDay(0);
//        }
    }

    public String getId()
    {
        return this.tags;
    }

    public void setTags(String tags)
    {
        this.tags = tags;
    }

    public String getLink()
    {
        return this.link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getLastActivity()
    {
        return this.lastActivity;
    }

//    public void setLastActivity(LocalDate lastActivity)
//    {
//        this.lastActivity = lastActivity;
//    }

    public void setLastActivity(String dateString) {
        this.lastActivity= dateString;
//        try {
//            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss.AAAZ");
//            this.lastActivity = LocalDate.parse(dateString, dateFormatter);
//        } catch (DateTimeParseException | IllegalArgumentException e) {
//            e.printStackTrace();
//            this.lastActivity = LocalDate.ofEpochDay(0);
//        }
    }

    public Integer getAnswerCount()
    {
        return this.answerCount;
    }

    public void setAnswerCount(Integer answerCount)
    {
        this.answerCount = answerCount;
    }

    @Override
    public String toString()
    {
        return "SOQuestion{" +
                "tags=" + tags +
                ", link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", lastActivity=" + lastActivity +
                ", creationDate=" + creationDate +
                ", answerCount=" + answerCount +
                '}';
    }
}
