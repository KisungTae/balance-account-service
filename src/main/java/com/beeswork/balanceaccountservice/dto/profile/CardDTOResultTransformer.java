package com.beeswork.balanceaccountservice.dto.profile;

import java.util.ArrayList;
import java.util.List;

public class CardDTOResultTransformer {

    private static final int ID = 0;
    private static final int NAME = 1;
    private static final int BIRTH_YEAR = 2;
    private static final int HEIGHT = 3;
    private static final int ABOUT = 4;
    private static final int DISTANCE = 5;
    private static final int PHOTO_KEY = 6;

    public static CardDTO map(List<Object[]> rows) {
        List<CardDTO> cardDTOs = mapList(rows);
        if (cardDTOs.size() <= 0) return null;
        else return cardDTOs.get(0);
    }

    public static List<CardDTO> mapList(List<Object[]> rows) {
        List<CardDTO> cardDTOs = new ArrayList<>();
        if (rows == null || rows.size() <= 0) return cardDTOs;

        CardDTO cardDTO = new CardDTO();
        String previousId = "";

        for (Object[] row : rows) {
            String id = row[ID].toString();
            if (!previousId.equals(id)) {
                cardDTOs.add(cardDTO);
                previousId = id;
                String name = row[NAME].toString();
                int birthYear = Integer.parseInt(row[BIRTH_YEAR].toString());
                int height = (int) row[HEIGHT];
                String about = row[ABOUT].toString();
                int distance = (int) ((double) row[DISTANCE]);
                cardDTO = new CardDTO(id, name, birthYear, height, about, distance);
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
