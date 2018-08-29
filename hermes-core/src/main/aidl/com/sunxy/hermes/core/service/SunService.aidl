// SunHermeService.aidl
package com.sunxy.hermes.core.service;

import com.sunxy.hermes.core.service.request;
import com.sunxy.hermes.core.service.Response;

interface SunService {
    Response send(in Request request);
}
