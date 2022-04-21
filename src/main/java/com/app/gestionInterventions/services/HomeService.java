package com.app.gestionInterventions.services;

import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.user.role.ERole;
import com.app.gestionInterventions.models.work.demand.Status;
import com.app.gestionInterventions.repositories.resources.material.MaterialRepositoryImpl;
import com.app.gestionInterventions.repositories.resources.team.TeamRepositoryImpl;
import com.app.gestionInterventions.repositories.user.UserRepositoryImpl;
import com.app.gestionInterventions.repositories.user.role.RoleRepository;
import com.app.gestionInterventions.repositories.work.demand.DemandRepositoryImpl;
import com.app.gestionInterventions.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class HomeService {
    @Autowired
    DemandRepositoryImpl demandRepository;
    @Autowired
    MaterialRepositoryImpl materialRepository;
    @Autowired
    TeamRepositoryImpl teamRepository;
    @Autowired
    UserRepositoryImpl userRepository;
    @Autowired
    RoleRepository roleRepository;

    public HomeManagerLoader homeManagerLoader(){
        return new HomeManagerLoader(
                this.demandRepository.CountDemandsByStatus(Status.In_Progress),
                teamRepository.countTeamByStatus(com.app.gestionInterventions.models.recources.team.Status.Available),
                userRepository.countByRole(this.roleRepository.findByName(ERole.ROLE_MEMBER).get()),
                materialRepository.countMaterialByStatus(com.app.gestionInterventions.models.recources.material.Status.Broken_down)
        );
    }
    public HomeCustomerLoader homeCustomerLoader(){
        UserDetailsImpl userDetails=(UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String id=userDetails.getId();
        return new HomeCustomerLoader(
                this.demandRepository.countUserDemandsByStatus(id,Status.Accepted),
                this.demandRepository.countUserDemandsByStatus(id,Status.Refused),
                this.demandRepository.countUserDemandsByStatus(id,Status.In_Progress)
        );
    }


    public class HomeCustomerLoader{
        private long countCurrentAcceptedDemands;
        private long countCurrentRefusedDemands;
        private long countCurrentdDemands;

        private HomeCustomerLoader(long countCurrentAcceptedDemands, long countCurrentRefusedDemands, long countCurrentdDemands) {
            this.countCurrentAcceptedDemands = countCurrentAcceptedDemands;
            this.countCurrentRefusedDemands = countCurrentRefusedDemands;
            this.countCurrentdDemands = countCurrentdDemands;
        }
        private HomeCustomerLoader(){}

        public long getCountCurrentAcceptedDemands() {
            return countCurrentAcceptedDemands;
        }

        public long getCountCurrentRefusedDemands() {
            return countCurrentRefusedDemands;
        }

        public long getCountCurrentdDemands() {
            return countCurrentdDemands;
        }
    }

    public class HomeManagerLoader{
        private long countCurrentDemands;
        private long countTeam;
        private long countEmployees;
        private long countMaterialsBrukenDown;

        private HomeManagerLoader(){}

        private HomeManagerLoader(long countCurrentDemands, long countTeam, long countemployees, long countMaterialsBrukenDown) {
            this.countCurrentDemands = countCurrentDemands;
            this.countTeam = countTeam;
            this.countEmployees = countemployees;
            this.countMaterialsBrukenDown = countMaterialsBrukenDown;
        }

        public long getCountCurrentDemands() {
            return countCurrentDemands;
        }

        public long getCountTeam() {
            return countTeam;
        }

        public long getCountemployees() {
            return countEmployees;
        }

        public long getCountMaterialsBrukenDown() {
            return countMaterialsBrukenDown;
        }
    }
}
