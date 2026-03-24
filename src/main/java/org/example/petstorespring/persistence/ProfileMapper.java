package org.example.petstorespring.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import org.springframework.context.annotation.Profile;

import org.example.petstorespring.entity.Profile;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileMapper extends BaseMapper<Profile> {
}
