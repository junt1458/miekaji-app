package jp.ac.gifu_nct.miekaji.utils

import android.util.Log
import jp.ac.gifu_nct.miekaji.structures.JobCategory
import jp.ac.gifu_nct.miekaji.structures.JobInfo
import jp.ac.gifu_nct.miekaji.structures.User
import jp.ac.gifu_nct.miekaji.utils.http.HTTPClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object DataUtil {

    var me: User? = null

    fun fetchData(endpoint: String, arguments: String): JSONObject {
        val req_url = "${AuthUtil.API_BASE_URL}${endpoint}?token=${AuthUtil.token}${arguments}"

        return HTTPClient.getRequest(req_url, null)
    }

    fun fetchTodayJob(): List<JobInfo> {
        val bufferList = ArrayList<JobInfo>()
        try {
            val jobs = fetchData("/job/today", "").getJSONArray("histories")
            for(i in 0 until jobs.length()) {
                val element = jobs.getJSONObject(i)
                val category = element.getJSONObject("category")
                bufferList.add(
                    JobInfo(
                        element.getLong("ID"),
                        JobCategory(
                            category.getLong("ID"),
                            category.getString("name"),
                            category.getString("detail"),
                            category.getDouble("weight")
                        ),
                        element.getDouble("motion"),
                        element.getDouble("time"),
                        element.getDouble("value")
                    )
                )
            }

            return bufferList
        } catch (e: JSONException) {
            return bufferList
        }
    }

    fun fetchTodayJobTimeValueByEach(): HashMap<JobCategory, Pair<Double, Double>> {
        return fetchTodayJobTimeValueByEach(fetchTodayJob())
    }

    fun fetchTodayJobTimeValueByEach(jobs: List<JobInfo>): HashMap<JobCategory, Pair<Double, Double>> {
        val map = HashMap<JobCategory, Pair<Double, Double>>()
        val categories = fetchCategories()
        categories.forEach {
            map[it] = Pair<Double, Double>(0.0, 0.0)
        }
        jobs.forEach {
            val value = map[it.jobCategory]!!
            map[it.jobCategory] = Pair<Double, Double>(value.first + it.jobValue, value.second + it.jobTime)
        }
        return map
    }

    fun fetchCategories(): List<JobCategory> {
        val bufferList = ArrayList<JobCategory>()
        try {
            val categories = fetchData("/job/list", "").getJSONArray("categories")
            for(i in 0 until categories.length()) {
                val element = categories.getJSONObject(i)
                bufferList.add(
                    JobCategory(
                        element.getLong("ID"),
                        element.getString("name"),
                        element.getString("detail"),
                        element.getDouble("weight")
                    )
                )
            }
            return bufferList
        } catch(e: JSONException) {
            return bufferList
        }
    }

    fun fetchMe(): User? {
        return try {
            val element = fetchData("/user/me", "")
            User(
                element.getLong("ID"),
                element.getString("name"),
                element.getString("icon_id"),
                false,
                element.getDouble("sum"),
                element.getDouble("today"),
                true
            )
        } catch(e: JSONException) {
            null
        }
    }

    fun fetchFriends(): List<User> {
        val groupMembers = fetchGroupMembers()
        val bufferList = ArrayList<User>()
        try {
            val categories = fetchData("/friends/list", "").getJSONArray("users")
            for(i in 0 until categories.length()) {
                val element = categories.getJSONObject(i) ?: continue
                val user = User(
                    element.getLong("ID"),
                    element.getString("name"),
                    element.getString("icon_id"),
                    false,
                    element.getDouble("sum"),
                    element.getDouble("today"),
                    false
                )
                user.isSameGroup = isUserInList(user, groupMembers)
                bufferList.add(user)
            }
            bufferList.sortBy { it.userName }
            bufferList.sortByDescending { it.jobSum }
            return bufferList
        } catch(e: JSONException) {
            return bufferList
        }
    }

    private fun isUserInList(user: User, list: List<User>):Boolean {
        list.forEach {
            if (user == it) return true
        }
        return false
    }

    fun fetchGroupMembers(): List<User> {
        val bufferList = ArrayList<User>()
        try {
            val categories = fetchData("/fun/info", "").getJSONArray("members")
            for(i in 0 until categories.length()) {
                val element = categories.getJSONObject(i)
                val user = User(
                    element.getLong("ID"),
                    element.getString("name"),
                    element.getString("icon_id"),
                    false,
                    element.getDouble("sum"),
                    element.getDouble("today"),
                    true
                )
                bufferList.add(user)
            }
            bufferList.sortBy { it.userName }
            bufferList.sortByDescending { it.jobSum }
            return bufferList
        } catch(e: JSONException) {
            return bufferList
        }
    }

}