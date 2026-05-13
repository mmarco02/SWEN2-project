package fh.swen.swen2tourplanner.persistence;

import fh.swen.swen2tourplanner.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends JpaRepository<User, Long> {

}
