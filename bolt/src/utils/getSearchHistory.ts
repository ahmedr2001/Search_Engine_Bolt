export default function getSearchHistory(): { [key: string]: number } {
	const searchHistory = localStorage.getItem("searchHistory");
	if (searchHistory) {
		return JSON.parse(searchHistory);
	}
	return {};
}
