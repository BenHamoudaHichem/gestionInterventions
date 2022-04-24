package com.app.gestionInterventions.services.statistics;

import com.app.gestionInterventions.repositories.work.demand.DemandRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DemandStatistic {

    @Autowired
    private DemandRepositoryImpl demandRepository;

    public List<DemandPerYear> getDemandPerYearList()
    {

        List<DemandPerYear> res = new ArrayList<DemandPerYear>(this.demandRepository.findStatsYear());
        for (int i =1 ; i <13 ; i++) {
            DemandPerYear demand=new DemandPerYear(i,0);
            if(!res.contains(demand))
            {
                res.add(demand);
            }
        }
        Collections.sort(res, Comparator.comparing(DemandPerYear::getMonth));
        return res;
    }



    public static class DemandPerYear{
        private long month;
        private long sum;

        public long getMonth() {
            return month;
        }

        public long getSum() {
            return sum;
        }

        public DemandPerYear(long month, long sum) {
            this.month = month;
            this.sum = sum;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DemandPerYear that = (DemandPerYear) o;
            return getMonth() == that.getMonth();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getMonth());
        }

        @Override
        public String toString() {
            return "DemandPerYear{" +
                    "month=" + month +
                    ", sum=" + sum +
                    '}';
        }
    }
}
