package smartspace.infra;

import java.util.List;

import smartspace.data.UserEntity;


public interface UserService {
	
	public UserEntity newUser(UserEntity entity);
	
	public List<UserEntity> getUsingPagination (int size, int page);

}
