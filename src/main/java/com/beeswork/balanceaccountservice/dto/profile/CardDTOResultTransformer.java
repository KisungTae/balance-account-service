package com.beeswork.balanceaccountservice.dto.profile;

import java.util.ArrayList;
import java.util.List;

public class CardDTOResultTransformer {

    private static final int CARD_ID         = 0;
    private static final int CARD_NAME       = 1;
    private static final int CARD_ABOUT      = 2;
    private static final int CARD_BIRTH_YEAR = 3;
    private static final int CARD_HEIGHT     = 4;
    private static final int CARD_DISTANCE   = 5;
    private static final int CARD_PHOTO_KEY  = 6;

    public static List<CardDTO> toList(List<Object[]> rows) {
        List<CardDTO> cardDTOs = new ArrayList<>();
        CardDTO cardDTO = new CardDTO();
        String previousId = "";

        for (Object[] row : rows) {
            String id = row[CARD_ID].toString();
            if (!previousId.equals(id)) {
                cardDTOs.add(cardDTO);
                previousId = id;
                cardDTO = new CardDTO(id,
                                      row[CARD_NAME].toString(),
                                      row[CARD_ABOUT].toString(),
                                      (int) row[CARD_HEIGHT],
                                      Integer.parseInt(row[CARD_BIRTH_YEAR].toString()),
                                      (int) ((double) row[CARD_DISTANCE]));
            }

            if (row[CARD_PHOTO_KEY] != null)
                cardDTO.getPhotoKeys().add(row[CARD_PHOTO_KEY].toString());
        }
        cardDTOs.add(cardDTO);
        cardDTOs.remove(0);
        return cardDTOs;
    }

}
