package com.AchadosPerdidos.API.Domain.Interfaces;

public interface IPhotoRepository {

    boolean existsByUrl(String url);
}
