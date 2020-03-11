package com.fsh.common.repository

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/11
 * description: TODO there need some info to descript current java file
 *
 */

object RepositoryProvider {
    private val repositoryMap:ConcurrentHashMap<Class<*>,BaseRepository> by lazy{
        ConcurrentHashMap<Class<*>,BaseRepository>()
    }
}