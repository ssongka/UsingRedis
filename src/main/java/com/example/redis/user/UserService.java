package com.example.redis.user;

import com.example.redis.client.repository.RedisEntityNullException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;


@Slf4j
@Service
public class UserService {

    private final UserDBRepository userDBRepository;

    private final UserRedisRepository userRedisRepository;

    public UserService(UserDBRepository userDBRepository, UserRedisRepository userRedisRepository) {
        this.userDBRepository = userDBRepository;
        this.userRedisRepository = userRedisRepository;
    }

    public User findByNo(long userNo) {
        //redis에서 먼저 조회해 본다
        try {
            User user = this.userRedisRepository.queryForObject(userNo);
            return isDummy(user) ? null : user;
        }
        catch (RedisEntityNullException e ) {
            Optional<User> userOptional = userDBRepository.findById(userNo);
            //db에서 찾았다면?
            if( userOptional.isPresent()) {
                User user = userOptional.get();

                //다음 cache hit를 위해 redis에 저장해준다
                boolean saveObject = userRedisRepository.saveObject(userNo, user);
                if(!saveObject) {
                    log.error("[UserService] saveObject fail.. userNo={}", userNo);
                    return null;
                }
            } else {
                //더미를 넣어서 cache에 넣어서 db 비용을 줄인다
                userRedisRepository.saveObject(userNo, new User(true));
            }
        }

        return null;
    }

    private boolean isDummy(User user) {
        //DB 호출 플래그가 on 이고 이름 비워져 있다면 더미라고 판단한다
        return user.isDatabaseLoaded() && StringUtils.isEmpty(user.getName());
    }

    public boolean save(User user) {
        User save = this.userDBRepository.save(user);
        return this.userRedisRepository.saveObject(save.getUserNo(), user);
    }
}
