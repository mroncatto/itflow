package io.github.mroncatto.itflow.application.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface IAbstractRepository<T, U extends Serializable> extends JpaRepository<T, U> {}
