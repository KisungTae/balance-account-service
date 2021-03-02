package com.beeswork.balanceaccountservice.dto.profile;

import java.util.ArrayList;
import java.util.List;

public class CardDTOResultTransformer {

    private static final int ID = 0;
    private static final int NAME = 1;
    private static final int BIRTH_YEAR = 2;
    private static final int GENDER = 3;
    private static final int HEIGHT = 4;
    private static final int ABOUT = 5;
    private static final int DISTANCE = 6;
    private static final int PHOTO_KEY = 7;

    public static List<CardDTO> map(List<Object[]> rows) {
        List<CardDTO> cardDTOs = new ArrayList<>();
        CardDTO cardDTO = new CardDTO();
        String previousId = "";

        for (Object[] row : rows) {
            String id = row[ID].toString();
            if (!previousId.equals(id)) {
                cardDTOs.add(cardDTO);
                previousId = id;
                String name = row[NAME].toString();
                int birthYear = Integer.parseInt(row[BIRTH_YEAR].toString());
                boolean gender = Boolean.parseBoolean(row[GENDER].toString());
                int height = (int) row[HEIGHT];
                String about = row[ABOUT].toString();
                int distance = (int) ((double) row[DISTANCE]);
                cardDTO = new CardDTO(id, name, birthYear, gender, height, about, distance);
            }

            Object photoKey = row[PHOTO_KEY];
            if (photoKey != null)
                cardDTO.getPhotoKeys().add(photoKey.toString());
        }
        cardDTOs.add(cardDTO);
        cardDTOs.remove(0);
        return cardDTOs;
    }

}
