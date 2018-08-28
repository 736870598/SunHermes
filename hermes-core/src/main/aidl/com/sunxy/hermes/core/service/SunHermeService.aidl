// SunHermeService.aidl
package com.sunxy.hermes.core.service;

import com.sunxy.hermes.core.service.request;
import com.sunxy.hermes.core.service.Responce;

interface SunHermeService {
    Responce send(in Request request);
}
