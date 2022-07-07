package com.beeswork.balanceaccountservice.service.count;

import com.beeswork.balanceaccountservice.dao.match.MatchDAO;
import com.beeswork.balanceaccountservice.dao.swipe.SwipeDAO;
import com.beeswork.balanceaccountservice.dto.count.CountTabDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CountServiceImpl implements CountService {

    private final SwipeDAO swipeDAO;
    private final MatchDAO matchDAO;

    @Autowired
    public CountServiceImpl(SwipeDAO swipeDAO, MatchDAO matchDAO) {
        this.swipeDAO = swipeDAO;
        this.matchDAO = matchDAO;
    }

    @Override
    public List<CountTabDTO> countTabs(UUID accountId) {
        //        try {
//            ExecutorService executorService = Executors.newFixedThreadPool(2);
//            Future<List<SwipeDTO>> listSwipesFuture = executorService.submit(listSwipeDTOsCallable);
//            Future<CountSwipesDTO> countSwipesFuture = executorService.submit(() -> countSwipes(swipedId));
//
//            ListSwipesDTO listSwipesDTO = new ListSwipesDTO();
//            List<SwipeDTO> swipeDTOs = listSwipesFuture.get(1, TimeUnit.MINUTES);
//            CountSwipesDTO countSwipesDTO = countSwipesFuture.get(1, TimeUnit.MINUTES);
//
//            listSwipesDTO.setSwipeDTOs(swipeDTOs);
//            listSwipesDTO.setSwipeCount(countSwipesDTO.getCount());
//            listSwipesDTO.setSwipeCountCountedAt(countSwipesDTO.getCountedAt());
//            return listSwipesDTO;
//        } catch (InterruptedException | ExecutionException | TimeoutException e) {
//            throw new InternalServerException();
//        }
        return null;
    }
}
