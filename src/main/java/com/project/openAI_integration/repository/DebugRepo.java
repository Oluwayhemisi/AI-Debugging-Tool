package com.project.openAI_integration.repository;

import com.project.openAI_integration.model.Debug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebugRepo extends JpaRepository<Debug,Long> {


}
