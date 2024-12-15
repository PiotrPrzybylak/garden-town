package org.commontech.gardentown.domain;

import org.commontech.gardentown.port.incoming.DeleteGardenUseCase;
import org.commontech.gardentown.port.outgoing.Garden;
import org.springframework.stereotype.Service;

@Service
record DeleteGardenService(Garden garden) implements DeleteGardenUseCase {
    @Override
    public void apply() {
        garden.delete();
    }
}
