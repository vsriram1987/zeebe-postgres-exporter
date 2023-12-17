package io.zeebe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public class DemoEntityRepository {


	@Repository
	public interface EmployeeRepository extends JpaRepository<DemoEntity, Long>{

	}
}
