// robot wrapper that contains robots.txt information
// @author Christopher Besser
package crawler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Robot
{
	private List<String> agents;
	private Map<String, ArrayList<String>> allowed;
	private Map<String, ArrayList<String>> banned;
	private Map<String, Integer> delay;
	/**
	 * constructor
	 */
	public Robot()
	{
		agents = new ArrayList<String>();
		banned = new HashMap<String, ArrayList<String>>();
		allowed = new HashMap<String, ArrayList<String>>();
		delay = new HashMap<String, Integer>();
	}

	/**
	 * check if contains agent
	 * @param agent - the agent to check for
	 * @return Returns true if this robot contains the agent
	 */
	public boolean containsAgent(String agent)
	{
		return this.agents.contains(agent);
	}

	/**
	 * add agent to agent list
	 * @param agent - the agent to add
	 */
	public void addAgent(String agent)
	{
		if (!this.agents.contains(agent))
		{
			this.agents.add(agent);
		}
	}

	/**
	 * add allowed url under given agent
	 * @param agent - the agent to add URL permission to
	 * @param allow - the URL to allow
	 */
	public void addAllow(String agent, String allow)
	{
		ArrayList<String> allowList = this.allowed.get(agent);
		if ((allowList == null) || (allowList.size() == 0))
		{
			ArrayList<String> newList = new ArrayList<String>();
			newList.add(allow);
			this.allowed.put(agent, newList);
		}
		else
		{
			if (!allowList.contains(allow))
			{
				allowList.add(allow);
			}
			this.allowed.put(agent, allowList);
		}
	}

	/**
	 * add banned url under given agent
	 * @param agent - the agent to add URL ban to
	 * @param ban - the URL to ban
	 */
	public void addBanned(String agent, String ban)
	{
		ArrayList<String> bannedList = this.banned.get(agent);
		if ((bannedList == null) || (bannedList.size() == 0))
		{
			ArrayList<String> newList = new ArrayList<String>();
			newList.add(ban);
			this.banned.put(agent, newList);
		}
		else
		{
			if (!bannedList.contains(ban))
			{
				bannedList.add(ban);
			}
			this.banned.put(agent, bannedList);
		}
	}

	/**
	 * add delay under given agent
	 * @param agent - the agent to add a delay to
	 * @param delay - the delay
	 */
	public void addDelay(String agent, int delay)
	{
		this.delay.put(agent, delay);
	}

	/**
	 * Retrieve URLs the agent allows
	 * @param agent - the agent to poll
	 * @return Returns the list of URLs the agent allows
	 */
	public List<String> getAllowed(String agent)
	{
		return this.allowed.get(agent);
	}

	/**
	 * Retrieve URLs the agent bans
	 * @param agent - the agent to poll
	 * @return Returns the list of URLs the agent bans
	 */
	public List<String> getBanned(String agent)
	{
		return this.banned.get(agent);
	}

	/**
	 * Retrieve all associated delays
	 * @param agent - the agent to poll
	 * @return Returns the list of associated delays
	 */
	public int getDelay(String agent)
	{
		return this.delay.get(agent);
	}
}