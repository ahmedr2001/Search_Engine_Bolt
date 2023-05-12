export default function addSearchQueryToHistory(query: string) {
	query = query.trim();
	const searchHistory = JSON.parse(
		localStorage.getItem("searchHistory") || "{}"
	);
	searchHistory[query] = (searchHistory[query] || 0) + 1;
	localStorage.setItem("searchHistory", JSON.stringify(searchHistory));
}
