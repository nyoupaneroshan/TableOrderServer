package com.example.tableorderserver.Module;

import java.util.List;

public class MyResponse {
    public long multicast_id;
    public int success, faliure, canonical_ids;
    public List<Result> results;
}
