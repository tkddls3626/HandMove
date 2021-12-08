package poly.service;

import java.util.List;
import java.util.Map;

import poly.dto.UserDTO;

public interface IUserService {

	UserDTO getUserInfo(UserDTO uDTO) throws Exception;

	int insertUserInfo(UserDTO uDTO) throws Exception;

	UserDTO idCheck(String userId) throws Exception;

	int updateJoinStudy(Map<String, String> sMap) throws Exception;

	int updateLeaveStudy(Map<String, String> pMap) throws Exception;

	int updateUserMbti(UserDTO pDTO) throws Exception;

	List<String> getUserMbti(List<String> list) throws Exception;

	
}
