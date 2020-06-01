package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.SyncDataDTO;

public interface SyncService {

    public SyncDataDTO getSyncData(String username);
}
