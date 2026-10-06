package example.roommate.Application.Service;

import example.roommate.Domain.model.Room.Key;
import example.roommate.DataBase.KeyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class KeyService {
    private KeyRepository keyRepository;

    public KeyService(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    public Optional<Key> findByUUID(UUID id){
        return keyRepository.findById(id);
    }

    public void saveKey(Key key){
        keyRepository.save(key);
    }
}
